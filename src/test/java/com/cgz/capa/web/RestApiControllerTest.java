package com.cgz.capa.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("file:src/main/webapp/WEB-INF/mvc-dispatcher-servlet.xml")
public class RestApiControllerTest {
    private MockMvc mockMvc;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    protected WebApplicationContext wac;

    @Before
    public void setup() {
        this.mockMvc = webAppContextSetup(this.wac).build();
    }

    @Test
    public void testPermissionsFromStore() throws Exception {
        mockMvc.perform(get("/capa/permissionsFromStore?packageName=com.ea.games.nfs13_row"))
                .andExpect(status().isOk());

    }

    @Test
    public void testPermissionsFromStoreBadRequest() throws Exception {
        mockMvc.perform(get("/capa/permissionsFromStore?packageName=com.ea.games.nfs13_row_BAD"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAnaliseFromStore() throws Exception {
        mockMvc.perform(get("/capa/analiseFromStore?packageName=com.ihandysoft.ledflashlight.mini"))
                .andExpect(status().isOk());

    }

    @Test
    public void testAnaliseFromStoreBadRequest() throws Exception {

        mockMvc.perform(get("/capa/analiseFromStore?packageName=com.ihandysoft.ledflashlight.mini_BAD"))
                .andExpect(status().isBadRequest());
    }
}
