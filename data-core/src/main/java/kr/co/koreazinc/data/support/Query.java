package kr.co.koreazinc.data.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.hibernate.procedure.internal.ProcedureCallImpl;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPADeleteClause;
import com.querydsl.jpa.impl.JPAInsertClause;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAUpdateClause;

import jakarta.persistence.Convert;
import jakarta.persistence.StoredProcedureQuery;
import kr.co.koreazinc.data.functional.NativeFormer;
import kr.co.koreazinc.data.support.attribute.ConverterFactory;
import kr.co.koreazinc.data.types.Condition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SuppressWarnings("unchecked")
public class Query<DTO> {

    protected final NativeQueryImpl<DTO> query;

    private static final ConverterFactory converterFactory = new ConverterFactory();

    public Query(jakarta.persistence.Query query) {
        this.query = query.unwrap(NativeQueryImpl.class);
    }

    public <T extends Query<DTO>> T bean(Class<? extends DTO> type, NativeFormer<DTO> former) {
        this.query.setTupleTransformer((tuple, aliases)->{
            try {
                DTO result = type.getDeclaredConstructor().newInstance();
                for (int i = 0; i < aliases.length; i++) {
                    String alias = former.transformAlias(aliases[i]);
                    Object value = tuple[i];
                    try {
                        Method setter = type.getMethod("set" + StringUtils.capitalize(alias), type.getDeclaredField(alias).getType());
                        Convert convert = AnnotationUtils.findAnnotation(setter, Convert.class);
                        if (!ObjectUtils.isEmpty(convert)) {
                            try {
                                value = converterFactory.getConverter(convert.converter()).convertToEntityAttribute(tuple[i]);
                            } catch (ClassCastException e) {
                                value = converterFactory.getConverter(convert.converter()).convertToEntityAttribute(String.valueOf(tuple[i]));
                            }
                        }
                        setter.invoke(result, value);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }
                return result;
            } catch (ReflectiveOperationException e) {
                throw new ExpressionException(type.getName(), e);
            }
        });
        return (T) this;
    }

    public <T extends Query<DTO>> T bean(Class<? extends DTO> type) {
        return this.bean(type, alias->alias);
    }

    public Query<DTO> condition(Condition condition) {
        // // Filter
        // query.where(condition.getFilter(fields));

        // if (!this.isCountQuery) {
        //     // Sort
        //     query.orderBy(condition.getSort(fields));

        //     // Pageable
        //     if (condition.pageable()) {
        //         query.offset(condition.getSkip());
        //         query.limit(condition.getTake());
        //     }
        // }
        return this;
    }

    private List<DTO> getResultList() {
        return query.getResultList();
    }

    private DTO getSingleResult() {
        return query.getSingleResult();
    }

    public int execute() {
        return query.executeUpdate();
    }

    public Stream<DTO> stream() {
        return this.getResultList().stream();
    }

    public DTO fetchOne() {
        return this.getSingleResult();
    }

    public List<DTO> fetch() {
        return this.getResultList();
    }

    public DTO fetchFirst() {
        return this.stream().findFirst().orElse(null);
    }

    public Long fetchCount() {
        return Long.valueOf(Optional.ofNullable(this.fetch()).orElse(new ArrayList<DTO>()).size());
    }

    public static class Select<DTO> {

        protected final JPAQuery<DTO> query;
        protected final Map<String, ComparableExpressionBase<?>> fields = new HashMap<>();
        private final boolean isCountQuery;

        public Select(JPAQuery<DTO> query) {
            this.query = query;
            this.isCountQuery = Number.class.isAssignableFrom(query.getType());
        }

        public Select(JPAQuery<DTO> query, boolean isCountQuery) {
            this.query = query;
            query.getType().getName();
            this.isCountQuery = isCountQuery;
        }

        protected void addField(String name, ComparableExpressionBase<?> field) {
            if (fields.containsKey(name)) {
                throw new ExpressionException("Field already exists: " + name);
            }
            fields.put(name, field);
        }

        public Select<DTO> condition(Condition condition) {
            // Filter
            query.where(condition.getFilter(fields));

            if (!this.isCountQuery) {
                // Sort
                query.orderBy(condition.getSort(fields));

                // Pageable
                if (condition.pageable()) {
                    query.offset(condition.getSkip());
                    query.limit(condition.getTake());
                }
            }
            return this;
        }

        public DTO fetchOne() {
            return query.fetchOne();
        }

        public List<DTO> fetch() {
            return query.fetch();
        }

        public DTO fetchFirst() {
            return query.fetchFirst();
        }

        public Stream<DTO> stream() {
            return query.stream();
        }

        public Long fetchCount() {
            return Long.valueOf(Optional.ofNullable(query.fetch()).orElse(new ArrayList<DTO>()).size());
        }
    }

    public static class Insert {

        protected final JPAInsertClause query;

        public Insert(JPAInsertClause query) {
            this.query = query;
        }

        @Transactional
        public long execute() {
            return query.execute();
        }
    }

    public static class Update {

        protected final JPAUpdateClause query;

        public Update(JPAUpdateClause query) {
            this.query = query;
        }

        @Transactional
        public long execute() {
            return query.execute();
        }
    }

    public static class Delete {

        protected final JPADeleteClause query;

        public Delete(JPADeleteClause query) {
            this.query = query;
        }

        @Transactional
        public long execute() {
            return query.execute();
        }
    }

    public static class Procedure<DTO> {

        protected ProcedureCallImpl<DTO> storedProcedure;

        public Procedure(StoredProcedureQuery storedProcedure) {
            this.storedProcedure = storedProcedure.unwrap(ProcedureCallImpl.class);
        }

        protected <T> T getValue(String name) {
            return (T) storedProcedure.getOutputParameterValue(name);
        }

        // TODO: Select to DTO Error
        public List<DTO> getResultList() {
            return storedProcedure.getResultList();
        }


        // private DTO getSingleResult() {
        //     return (DTO) storedProcedure.getSingleResult();
        // }

        // private Stream<DTO> stream() {
        //     return this.getResultList().stream();
        // }

        // private DTO fetchOne() {
        //     return this.getSingleResult();
        // }

        // private List<DTO> fetch() {
        //     return this.getResultList();
        // }

        // private DTO fetchFirst() {
        //     return this.stream().findFirst().orElse(null);
        // }

        // private Long fetchCount() {
        //     return Long.valueOf(Optional.ofNullable(this.fetch()).orElse(new ArrayList<DTO>()).size());
        // }

        public boolean execute() {
            return storedProcedure.execute();
        }
    }
}