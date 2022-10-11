package com.dish.wholesale.core.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {

    @Test
    public void testState() {
        State stateNew = State.CALIFORNIA;
        assertEquals(State.valueOfName("California"), stateNew);
    }

    @Test
    public void testStateAnother() {
        State state = State.COLORADO;
        assertEquals(State.valueOfName("Colorado"), state);

    }

    @Test
    public void testStateAbb() {
        State state = State.NEVADA;
        assertEquals(State.valueOfName("Nevada").getAbbreviation(), state.getAbbreviation());
    }

    @Test
    public void testStateException() {
       State state = State.UNKNOWN;
       assertEquals(State.valueOfName("alaska india"), state);
    }

    @Test
    public void testStateStringConvert() {
        State state = State.FLORIDA;
        assertEquals(State.valueOfName("florida").toString(), state.toString());
    }
}