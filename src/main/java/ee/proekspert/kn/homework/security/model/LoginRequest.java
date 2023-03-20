package ee.proekspert.kn.homework.security.model;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@RequiredArgsConstructor
public class LoginRequest {

    private final String username;
    private final String credentials;
}
