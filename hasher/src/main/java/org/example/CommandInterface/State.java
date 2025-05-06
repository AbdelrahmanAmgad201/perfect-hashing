package org.example.CommandInterface;

public interface State {
    public void excute();
    public State nextState();
}
