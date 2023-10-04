package com.ister.isterservlet.common;

public enum RequestStatus {
    Successful,
    AlreadyExists,
    DoesNotExist,
    DatabaseClosedConnection,
    WrongInformation,
    Failed;
}
