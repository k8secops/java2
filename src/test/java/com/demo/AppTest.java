package com.demo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @BeforeAll
    static void setup() {
        App.NAME = "TestUser";
        App.PASSWORD = "TestPass";
    }

    @Test
    void rendersNameAndPassword() {
        assertEquals("My name is TestUser and my password is TestPass", App.renderPage());
    }
}
