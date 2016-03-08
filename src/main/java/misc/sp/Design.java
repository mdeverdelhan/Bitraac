
package misc.sp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Design {
    
    private String designId;
    
    public Design() {
    }

    public void setDesignId(String designId) {
        this.designId = designId;
    }

    public String getDesignId() {
        return designId;
    }
}
