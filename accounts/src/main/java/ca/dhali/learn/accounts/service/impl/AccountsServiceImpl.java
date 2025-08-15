package ca.dhali.learn.accounts.service.impl;

import ca.dhali.learn.accounts.constants.AccountsConstants;
import ca.dhali.learn.accounts.dto.AccountsDTO;
import ca.dhali.learn.accounts.dto.CustomerDTO;
import ca.dhali.learn.accounts.entity.Accounts;
import ca.dhali.learn.accounts.entity.Customer;
import ca.dhali.learn.accounts.exception.CustomerAlreadyExistsException;
import ca.dhali.learn.accounts.exception.ResourceNotFoundException;
import ca.dhali.learn.accounts.mapper.AccountsMapper;
import ca.dhali.learn.accounts.mapper.CustomerMapper;
import ca.dhali.learn.accounts.repository.AccountsRepository;
import ca.dhali.learn.accounts.repository.CustomerRepository;
import ca.dhali.learn.accounts.service.IAccountsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;

    /**
     * @param customerDTO Customer DTO Object
     */
    @Override
    public void createAccount(CustomerDTO customerDTO) {
        Customer customer = CustomerMapper.mapToCustomer(customerDTO, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDTO.getMobileNumber());
        if(optionalCustomer.isPresent()){
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number "
            + customerDTO.getMobileNumber());
        }

        Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    /**
     * @param customer - Customer Object
     * @return the new account details
     */
    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);

        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }


    @Override
    public CustomerDTO fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", "" + customer.getCustomerId())
        );

        CustomerDTO customerDTO = CustomerMapper.mapToCustomerDto(customer, new CustomerDTO());
        customerDTO.setAccountsDTO(AccountsMapper.mapToAccountsDto(accounts, new AccountsDTO()));
        return customerDTO;
    }

    @Override
    public boolean updateAccount(CustomerDTO customerDto) {
        boolean isUpdated = false;
        AccountsDTO accountsDto = customerDto.getAccountsDTO();
        if(accountsDto !=null ){
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber()).orElseThrow(
                    () -> new ResourceNotFoundException("Account", "AccountNumber", "" + accountsDto.getAccountNumber())
            );
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId).orElseThrow(
                    () -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString())
            );
            CustomerMapper.mapToCustomer(customerDto,customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return  isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber)
        );
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }
}
