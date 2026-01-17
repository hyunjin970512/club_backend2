package kr.co.koreazinc.data.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.core.types.dsl.Expressions;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nonnull;
import kr.co.koreazinc.data.utils.DevExtremeUtils;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Schema(description = "DevExtreme 검색 조건")
public class Condition {

    @Builder.Default
    @Schema(description = "건너뛸 레코드 수")
    private long skip = 0L;

    @Builder.Default
    @Schema(description = "가져올 레코드 수")
    private long take = 0L;

    @Schema(description = "필터 조건")
    private String filter;

    @Schema(description = "총 페이지 수 표시 여부")
    private Boolean requireTotalCount;

    @Schema(description = "정렬 기준")
    private String sort;

    @Schema(description = "사용자 정의 파라미터")
    private String customQueryParams;

    public long getPage() {
        return this.skip / this.take;
    }

    public boolean pageable() {
        return this.take > 0;
    }

    public BooleanExpression getFilter(Map<String, ComparableExpressionBase<?>> fields) {
        if (StringUtils.hasText(this.filter)) {
            return parseFilter(this.filter, fields);
        }
        return Expressions.asBoolean(true).isTrue();
    }

    private BooleanExpression parseFilter(@NonNull String filter, Map<String, ComparableExpressionBase<?>> fields) {
        JsonNode parsedFilter = DevExtremeUtils.parseFilter(filter);
        FilterExpression expression = new FilterExpression();
        if (Filter.isSupport(parsedFilter)) {
            Filter condition = new Filter(parsedFilter);
            if (fields.containsKey(condition.getFieldName())) {
                ComparableExpressionBase<?> field = fields.get(condition.getFieldName());
                if (condition.getValue() == null || condition.getValue().isNull()) {
                    switch (condition.getOperator()) {
                        case "=":
                            expression.push(field.isNull());
                            break;
                        case "!=":
                        case "<>":
                            expression.push(field.isNotNull());
                            break;
                        default:
                            // 그 외 연산자는 null과 사용할 수 없으므로 무시하거나 예외 처리
                            break;
                    }
                } else {
                    expression.push(FilterOperator.from(field, condition.getOperator()).apply(field, condition.getValue()));
                }
            } else {
                // TODO: 필드가 존재하지 않는 경우 처리
            }
        } else {
            for(JsonNode node : parsedFilter) {
                if (node.isArray()) {
                    expression.push(this.parseFilter(node.toString(), fields));
                }
                if (node.isTextual()) {
                    expression.push(node.asText().toLowerCase());
                }
            }
        }
        return expression.calculate();
    }

    public OrderSpecifier<?>[] getSort(Map<String, ComparableExpressionBase<?>> fields) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        if (StringUtils.hasText(this.sort)) {
            Set<Sort> sorts = DevExtremeUtils.parseSort(this.sort);
            for (Sort sort : sorts) {
                if (fields.containsKey(sort.getSelector())) {
                    ComparableExpressionBase<?> field = fields.get(sort.getSelector());
                    if (sort.isDesc()) {
                        orderSpecifiers.add(field.desc());
                    } else {
                        orderSpecifiers.add(field.asc());
                    }
                } else {
                    // TODO: 필드가 존재하지 않는 경우 처리
                }
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }

    public <T> T getCustomQueryParams(Class<T> clazz) {
        if (StringUtils.hasText(this.customQueryParams)) {
            return DevExtremeUtils.parseCustomQueryParams(clazz, this.customQueryParams);
        }
        try {
            return clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            log.warn("Failed to create instance of {}: {}", clazz.getName(), e.getMessage());
        }
        return null;
    }

    public <T> T updateCustomQueryParams(@Nonnull T value, Class<T> clazz) {
        if (StringUtils.hasText(this.customQueryParams)) {
            try {
                return DevExtremeUtils.updateCustomQueryParams(value, this.customQueryParams);
            } catch (JacksonException e) {
                throw new RuntimeException(e);
            }
        }
        return value;
    }

    // TODO: 프로시저용 필터 파싱
    public String getNormalizedJsonFilter() {
        try {
            if (this.filter == null || this.filter.trim().isEmpty()) {
                return "";
            }
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(this.filter);

            JsonNode parsedFilter = flatten(root, mapper);

            String result = "";
            if (parsedFilter != null ) {
                if(parsedFilter.isObject()){
                    ArrayNode wrapped = mapper.createArrayNode();
                    wrapped.add(parsedFilter);
                    result = wrapped.toString();
                }else if(Filter.isSupport(parsedFilter)){
                    result = "[" + toConditionObject(parsedFilter,mapper).toString() + "]";
                }else{
                    result = parsedFilter.toString();
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("필터 파싱 중 오류 발생: " + e.getMessage(), e);
        }
    }

    public ArrayNode flatten(JsonNode node, ObjectMapper mapper) {
        ArrayNode result = mapper.createArrayNode();

        if (node.isArray()) {
            for (JsonNode element : node) {
                if (element.isArray()) {
                    if (Filter.isSupport(element)) {
                        result.add(toConditionObject(element,mapper));
                    } else {
                        // 중첩된 복합 조건 → 재귀
                        ArrayNode nested = flatten(element, mapper);
                        result.addAll(nested);
                    }
                } else if (element.isTextual()) {
                    result.add(element.asText()); // "and", "or"
                }
            }
        }

        return result;
    }

    private ObjectNode toConditionObject(JsonNode arrayNode, ObjectMapper mapper) {
        ObjectNode condition = mapper.createObjectNode();
        condition.put("column", arrayNode.get(0).asText());
        condition.put("op", arrayNode.get(1).asText());
        condition.put("value", arrayNode.get(2).isNull() ? "" : arrayNode.get(2).asText());
        return condition;
    }
}