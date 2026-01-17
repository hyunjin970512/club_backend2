package kr.co.koreazinc.doc.v3.models;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;

import com.fasterxml.jackson.annotation.JsonProperty;

import kr.co.koreazinc.doc.v3.models.parameters.Parameter;
import kr.co.koreazinc.doc.v3.models.servers.Server;
import kr.co.koreazinc.doc.v3.support.Extensions;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * PathItem
 *
 * @see     io.swagger.v3.oas.models.PathItem
 */

@Getter
@NoArgsConstructor
public class PathItem {

    private String summary;

    private String description;

    private Operation get;

    private Operation put;

    private Operation post;

    private Operation delete;

    private Operation options;

    private Operation head;

    private Operation patch;

    private Operation trace;

    private List<Server> servers;
    
    private List<Parameter> parameters;

    @JsonProperty("$ref")
    private String reference;

    @JsonProperty("x-" + Extensions.NAME)
    private Extensions extensions = new Extensions();

    public Map<HttpMethod, Operation> getOperations() {
        Map<HttpMethod, Operation> operations = new LinkedHashMap<>();
        if (!(this.get     == null)) operations.put(HttpMethod.GET, this.get.setOrder(1));
        if (!(this.put     == null)) operations.put(HttpMethod.PUT, this.put.setOrder(2));
        if (!(this.post    == null)) operations.put(HttpMethod.POST, this.post.setOrder(3));
        if (!(this.patch   == null)) operations.put(HttpMethod.PATCH, this.patch.setOrder(4));
        if (!(this.delete  == null)) operations.put(HttpMethod.DELETE, this.delete.setOrder(5));
        if (!(this.options == null)) operations.put(HttpMethod.OPTIONS, this.options.setOrder(6));
        if (!(this.head    == null)) operations.put(HttpMethod.HEAD, this.head.setOrder(7));
        if (!(this.trace   == null)) operations.put(HttpMethod.TRACE, this.trace.setOrder(8));
        return operations;
    }
}