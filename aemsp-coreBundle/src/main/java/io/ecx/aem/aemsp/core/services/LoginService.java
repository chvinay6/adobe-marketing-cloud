package io.ecx.aem.aemsp.core.services;

import io.ecx.aem.aemsp.core.vo.account.AccountVO;

public interface LoginService
{
    public AccountVO getUserData(String username, String password);
}
