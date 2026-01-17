package kr.co.koreazinc.data.repository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.koreazinc.data.model.attribute.BaseEnum;
import kr.co.koreazinc.data.model.attribute.converter.BaseEnumConverter;

@Repository
@Transactional(readOnly = true)
public class BaseEnumRepository {

    public class SelectQuery<DTO extends BaseEnum.Setter> {

        private Class<DTO> type;

        private Stream<DTO> results;

        public SelectQuery(Class<DTO> type) {
            this.type = type;
        }

        public SelectQuery<DTO> setClassName(String className) {
            try {
                this.results = Arrays.stream(Class.forName(className).getEnumConstants())
                    .map(dto -> (BaseEnum) dto)
                    .filter(entity -> !"@".equalsIgnoreCase(entity.getValue()))
                    .map(entity -> new BaseEnumConverter(entity).to(type));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + className, e);
            }
            return this;
        }

        public List<DTO> fetch() {
            return this.results.toList();
        }

        public Stream<DTO> stream() {
            return this.results;
        }
    }

    public <T extends BaseEnum.Setter> SelectQuery<T> selectQuery(Class<T> type) {
        return new SelectQuery<>(type);
    }
}