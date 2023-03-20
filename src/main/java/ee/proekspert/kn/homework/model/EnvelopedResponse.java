package ee.proekspert.kn.homework.model;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class EnvelopedResponse<R> {
    public static final String MSG_SUCCESS = "success";
    // Not used for now; 
    // In future implementation need to define our own error structure
    public static final String MSG_ERROR = "error";
    
    private final String status = MSG_SUCCESS;
    
    private Integer pageSize;
    private Integer pageNumber;
    private Integer itemCount;
    private Integer totalPages;
    private Long totalSize;
    
    private R payload;
}
