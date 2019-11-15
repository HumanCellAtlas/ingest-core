package org.humancellatlas.ingest.security;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class TestUserWhitelist {

    @Test
    public void testLists() {
        //given:
        UserWhiteList userWhiteList = new UserWhiteList("trusteddomain.com", "friendlypeople.net");

        //expect:
        asList("goodguy@trusteddomain.com", "upstandinglass@friendlypeople.net", "cooldude@friendlypeople.net")
                .forEach(email -> assertThat(userWhiteList.lists(email)).isTrue());

        //and:
        asList("maninavan@shadycharacters.tv", "suspicious@darkcorner.xyz")
                .forEach(email -> assertThat(userWhiteList.lists(email)).isFalse());
    }

}
