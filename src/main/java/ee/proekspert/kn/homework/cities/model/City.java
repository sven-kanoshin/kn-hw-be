package ee.proekspert.kn.homework.cities.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import ee.proekspert.kn.homework.model.AbtractPermissible;

@Getter
@Entity
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class City extends AbtractPermissible {    
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    
    @Id
    @JsonIgnore
    @Column(name = COL_ID)
    private long id;
    
    @JsonProperty("name")
    @Column(name = COL_NAME)
    private String name;
    
    @JsonProperty("img_url")
    @Column(columnDefinition="TEXT")
    private String imgUrl;
    
    @Column
    @JsonProperty("reference")
    private String reference;
}
