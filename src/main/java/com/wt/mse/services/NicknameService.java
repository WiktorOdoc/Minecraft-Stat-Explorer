package com.wt.mse.services;

import java.io.IOException;

public interface NicknameService {
    String nickFromUUID(String rawUuid) throws IOException;
}
