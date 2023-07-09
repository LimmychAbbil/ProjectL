package net.lim.model.connection;

import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StubConnectionTest {

    private StubConnection stubConnection;

    @BeforeEach
    public void setUp() {
        stubConnection = new StubConnection();
    }

    @Test
    void testValidateConnection() {
        Assertions.assertTrue(stubConnection.validateConnection());
    }

    @Test
    void testLoginAlwaysSuccess() {
        Assertions.assertTrue(stubConnection.login("any", "any"));
    }

    @Test
    void testSendRegistrationIsAlwaysOK() {
        Assertions.assertEquals(Response.Status.OK.getStatusCode(),
                stubConnection.sendRegistration("any", "any"));
    }

    @Test
    void testAllVersionsSupported() {
        Assertions.assertTrue(stubConnection.validateVersionSupported("anyFakeVersion"));
    }

    @Test
    void testAdvListIsEmpty() {
        Assertions.assertTrue(stubConnection.getAdvs().isEmpty());
    }

    @Test
    void testServerLaunchCommandStub() {
        Assertions.assertEquals("echo 'Stub connection is used'",
                stubConnection.getServerLaunchCommand(null));
    }

    @Test
    void testBackgroundImageNull() {
        Assertions.assertNull(stubConnection.getBackgroundImageName());
    }

    @Test
    void testServerInfoIsNull() {
        Assertions.assertNull(stubConnection.getServersInfoJSON());
    }

    @Test
    void notAllowToGetFileServerInfo() {
        Assertions.assertThrows(IllegalStateException.class, () -> stubConnection.getFileServerInfo());
    }

    @Test
    void notAllowToGetIgnoredFiles() {
        Assertions.assertThrows(IllegalStateException.class, () -> stubConnection.getIgnoredFilesInfo());
    }

    @Test
    void notAllowToGetFileHash() {
        Assertions.assertThrows(IllegalStateException.class, () -> stubConnection.getFullHashInfo());
    }

    @Test
    void testIsClosedIsTrueFromBeginning() {
        Assertions.assertTrue(stubConnection.isClosed());
    }

    @Test
    void testErrorMessageIsEmptyForOKStatus() {
        Assertions.assertEquals("", Connection.getErrorMessage(200));
    }

    @Test
    void testErrorMessageForUsernameIsTaken() {
        Assertions.assertEquals("Username is taken", Connection.getErrorMessage(507));
    }

    @Test
    void testErrorMessageForBanned() {
        Assertions.assertEquals("You are banned to register new accounts",
                Connection.getErrorMessage(508));
    }

    @Test
    void testDefaultMessageForUnknownCodes() {
        Assertions.assertEquals("Unknown error", Connection.getErrorMessage(-1));
        Assertions.assertEquals("Unknown error", Connection.getErrorMessage(-9999));
    }
}