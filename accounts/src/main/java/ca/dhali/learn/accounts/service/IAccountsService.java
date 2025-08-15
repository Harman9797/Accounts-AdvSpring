package ca.dhali.learn.accounts.service;

import ca.dhali.learn.accounts.dto.CustomerDTO;

public interface IAccountsService {

    /**
     *
     * @param customerDTO Customer DTO Object
     */
    void createAccount(CustomerDTO customerDTO);

    CustomerDTO fetchAccount(String mobileNumber);

    boolean updateAccount(CustomerDTO customerDTO);

    boolean deleteAccount(String mobileNumber);
}
