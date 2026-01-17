package kr.co.koreazinc.doc.v3.models.media;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import io.swagger.v3.oas.models.annotations.OpenAPI30;
import kr.co.koreazinc.doc.v3.models.ExternalDocumentation;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Schema
 *
 * @see     io.swagger.v3.oas.models.media.Schema
 * @see     io.swagger.v3.oas.annotations.media.Schema
 */

@Getter
@NoArgsConstructor
public class Schema {

    // REQUIRED.
    private String name;

    private String title;

    private String description;

    // "Object > (FieldName - Value)"
    private Map<String, Schema> properties = Map.of();

    // "Array > Item"
    private Schema items;

    // "Map > Value"
    private Schema additionalProperties;

    @JsonProperty("$ref")
    private String reference;

    private Object example;

    private boolean exampleSetFlag = false;

    @OpenAPI30
    private Type type = Type.OBJECT;

    private String format;
    /*
    |  Name      |  Type     |  Format    |  Comments
    |  integer   |  integer  |  int32     |  signed 32 bits
    |  long      |  integer  |  int64     |  signed 64 bits
    |  float     |  number   |  float     |
    |  double    |  number   |  double    |
    |  string    |  string   |            |
    |  byte      |  string   |  byte      |  base64 encoded characters
    |  binary    |  string   |  binary    |  any sequence of octets
    |  boolean   |  boolean  |            |
    |  date      |  string   |  date      |  As defined by full-date - RFC3339
    |  dateTime  |  string   |  date-time |  As defined by date-time - RFC3339
    |  password  |  string   |  password  |  A hint to UIs to obscure input.
    */

    @OpenAPI30
    private boolean nullable = true;

    private boolean readOnly = false;

    private boolean writeOnly = false;

    private boolean deprecated = false;

    private List<String> required = List.of();

    @JsonProperty("enum")
    private List<String> enumValue = List.of();

    private ExternalDocumentation externalDocs;


    // protected T _default;
    // protected T _const;

    // private BigDecimal multipleOf;
    // private BigDecimal maximum;
    // @OpenAPI30
    // private boolean exclusiveMaximum;
    // private BigDecimal minimum;
    // @OpenAPI30
    // private boolean exclusiveMinimum;
    // private Integer maxLength;
    // private Integer minLength;
    // private String pattern;
    // private Integer maxItems;
    // private Integer minItems;
    // private boolean uniqueItems;
    // private Integer maxProperties;
    // private Integer minProperties;
    // @OpenAPI30
    // private Schema not;
    // private XML xml;
    // private Discriminator discriminator;

    // @OpenAPI31
    // private List<Schema> prefixItems;
    // private List<Schema> allOf;
    // private List<Schema> anyOf;
    // private List<Schema> oneOf;

    // @OpenAPI31
    // private Set<String> types;

    // @OpenAPI31
    // private Map<String, Schema> patternProperties;

    // @OpenAPI31
    // private BigDecimal exclusiveMaximumValue;

    // @OpenAPI31
    // private BigDecimal exclusiveMinimumValue;

    // @OpenAPI31
    // private Schema contains;

    // @OpenAPI31
    // private String $id;

    // @OpenAPI31
    // private String $schema;

    // @OpenAPI31
    // private String $anchor;

    // @OpenAPI31
    // private String $vocabulary;

    // @OpenAPI31
    // private String $dynamicAnchor;

    // @OpenAPI31
    // private String contentEncoding;

    // @OpenAPI31
    // private String contentMediaType;

    // @OpenAPI31
    // private Schema contentSchema;

    // @OpenAPI31
    // private Schema propertyNames;

    // @OpenAPI31
    // private Schema unevaluatedProperties;

    // @OpenAPI31
    // private Integer maxContains;

    // @OpenAPI31
    // private Integer minContains;

    // @OpenAPI31
    // private Schema additionalItems;

    // @OpenAPI31
    // private Schema unevaluatedItems;

    // @OpenAPI31
    // private Schema _if;

    // @OpenAPI31
    // private Schema _else;

    // @OpenAPI31
    // private Schema then;

    // @OpenAPI31
    // private Map<String, Schema> dependentSchemas;

    // @OpenAPI31
    // private Map<String, List<String>> dependentRequired;

    // @OpenAPI31
    // private String $comment;

    // @OpenAPI31
    // private List<T> examples;

    // @OpenAPI31
    // private boolean booleanSchemaValue;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static enum Type {
        STRING("string"),
        NUMBER("number"),
        INTEGER("integer"),
        BOOLEAN("boolean"),
        ARRAY("array"),
        MAP("map"),
        OBJECT("object");

        @JsonValue
        private String value;
    }

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public Schema setName(String name) {
        this.name = name;
        return this;
    }

    public List<Schema> getChildren() {
        List<Schema> children = new ArrayList<>();
        switch (this.type) {
            case OBJECT:
                for (String name : this.properties.keySet()) {
                    children.add(this.properties.get(name).setName(name));
                }
                break;
            case ARRAY:
                if (this.items != null) {
                    this.items.setName("items");
                    children.add(this.items);
                }
                break;
            case MAP:
                if (this.additionalProperties != null) {
                    children.add(this.additionalProperties);
                }
                break;
            default:
                break;
        }
        return children;
    }
}