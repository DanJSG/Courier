package com.jsg.chatterbox.types;

import com.jsg.chatterbox.Chatterbox;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = Chatterbox.class)
@TestPropertySource(locations = "/chatterbox.test.env")
public class ChatTest {



}
