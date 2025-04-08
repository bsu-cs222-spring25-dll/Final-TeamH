package edu.bsu.cs.livestatus;

public interface LiveStatusProvider {
    String getLiveStatus(String username) throws Exception;
}
