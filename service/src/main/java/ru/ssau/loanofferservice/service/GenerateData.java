package ru.ssau.loanofferservice.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ru.ssau.loanofferservice.dto.BankDto;
import ru.ssau.loanofferservice.dto.CreditDto;
import ru.ssau.loanofferservice.dto.LoanOfferDto;
import ru.ssau.loanofferservice.dto.UserDto;
import ru.ssau.loanofferservice.jpa.dao.BankDaoService;
import ru.ssau.loanofferservice.jpa.dao.CreditDaoService;
import ru.ssau.loanofferservice.jpa.dao.UserDaoService;
import ru.ssau.loanofferservice.jpa.entity.Bank;
import ru.ssau.loanofferservice.jpa.entity.Credit;
import ru.ssau.loanofferservice.jpa.entity.User;
import ru.ssau.loanofferservice.service.impl.LoanOfferService;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GenerateData {

    private final CreditDaoService creditDaoService;
    private final UserDaoService userDaoService;
    private final BankDaoService bankDaoService;
    private final LoanOfferService loanOfferService;
    private final ModelMapper mapper;

    @PostConstruct
    public void init() {
        Credit credit1 = creditDaoService.findById(UUID.fromString("a111d86a-695d-11ed-a1eb-0242ac120002"));
        Credit credit2 = creditDaoService.findById(UUID.fromString("b211d86a-695d-11ed-a1eb-0242ac120002"));
        Credit credit3 = creditDaoService.findById(UUID.fromString("c311d86a-695d-11ed-a1eb-0242ac120002"));

        /*===================================================================================================*/
        Bank sberbank = bankDaoService.findById(UUID.fromString("1116247c-6953-11ed-a1eb-0242ac120002"));
        User user = userDaoService.findById(UUID.fromString("d87b6c18-431b-4db6-bfd1-3845f30350c6"));
        User user1 = userDaoService.findById(UUID.fromString("1eeb6c18-431b-4db6-bfd1-3845f30350c6"));
        User user2 = userDaoService.findById(UUID.fromString("2ffb6c18-431b-4db6-bfd1-3845f30350c6"));
        LoanOfferDto loanOfferDto = LoanOfferDto.builder()
                .bank(mapper.map(sberbank, BankDto.class))
                .user(mapper.map(user, UserDto.class))
                .credit(mapper.map(credit1, CreditDto.class))
                .build();
        LoanOfferDto loanOfferDto1 = LoanOfferDto.builder()
                .bank(mapper.map(sberbank, BankDto.class))
                .user(mapper.map(user1, UserDto.class))
                .credit(mapper.map(credit2, CreditDto.class))
                .build();
        LoanOfferDto loanOfferDto2 = LoanOfferDto.builder()
                .bank(mapper.map(sberbank, BankDto.class))
                .user(mapper.map(user2, UserDto.class))
                .credit(mapper.map(credit3, CreditDto.class))
                .build();
        loanOfferService.create(loanOfferDto, null);
        loanOfferService.create(loanOfferDto1, null);
        loanOfferService.create(loanOfferDto2, null);

        /*===================================================================================================*/
        Bank alfaBank = bankDaoService.findById(UUID.fromString("2226247c-6953-11ed-a1eb-0242ac120002"));
        User user3 = userDaoService.findById(UUID.fromString("b35fd86a-695d-11ed-a1eb-0242ac120002"));
        LoanOfferDto loanOfferDto3 = LoanOfferDto.builder()
                .bank(mapper.map(alfaBank, BankDto.class))
                .user(mapper.map(user3, UserDto.class))
                .credit(mapper.map(credit1, CreditDto.class))
                .build();
        loanOfferService.create(loanOfferDto3, null);

        /*===================================================================================================*/
        Bank tinkoff = bankDaoService.findById(UUID.fromString("3336247c-6953-11ed-a1eb-0242ac120002"));
        User user4 = userDaoService.findById(UUID.fromString("b36fd86a-695d-11ed-a1eb-0242ac120002"));
        LoanOfferDto loanOfferDto4 = LoanOfferDto.builder()
                .bank(mapper.map(tinkoff, BankDto.class))
                .user(mapper.map(user4, UserDto.class))
                .credit(mapper.map(credit2, CreditDto.class))
                .build();
        loanOfferService.create(loanOfferDto4, null);

        /*===================================================================================================*/
        Bank gazBank = bankDaoService.findById(UUID.fromString("4446247c-6953-11ed-a1eb-0242ac120002"));
        User user5 = userDaoService.findById(UUID.fromString("b37fd86a-695d-11ed-a1eb-0242ac120002"));
        LoanOfferDto loanOfferDto5 = LoanOfferDto.builder()
                .bank(mapper.map(gazBank, BankDto.class))
                .user(mapper.map(user5, UserDto.class))
                .credit(mapper.map(credit1, CreditDto.class))
                .build();
        loanOfferService.create(loanOfferDto5, null);

        /*===================================================================================================*/
        Bank bankView = bankDaoService.findById(UUID.fromString("5556247c-6953-11ed-a1eb-0242ac120002"));
        User user6 = userDaoService.findById(UUID.fromString("b38fd86a-695d-11ed-a1eb-0242ac120002"));
        LoanOfferDto loanOfferDto6 = LoanOfferDto.builder()
                .bank(mapper.map(bankView, BankDto.class))
                .user(mapper.map(user6, UserDto.class))
                .credit(mapper.map(credit3, CreditDto.class))
                .build();
        loanOfferService.create(loanOfferDto6, null);
    }
}
