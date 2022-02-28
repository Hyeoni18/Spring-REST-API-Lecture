package hello.springboot.springrestapi.index;

import hello.springboot.springrestapi.common.BaseTest;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class IndexControllerTest extends BaseTest {

    @Test
    public void index() throws Exception {
        //index 화면에 들어가면 각 리소스에 대한 루트가 나오길 원함. (현재 event 뿐임.)
        // /api/event
        this.mockMvc.perform(get("/api/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.events").exists());
    }
}
