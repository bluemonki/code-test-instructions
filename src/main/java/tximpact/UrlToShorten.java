package tximpact;

import lombok.Setter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.*;

@NoArgsConstructor
@Setter 
public class UrlToShorten {

    @JsonProperty("fullUrl")
    String fullUrl;
    @JsonProperty("customAlias")
    String customAlias;
}