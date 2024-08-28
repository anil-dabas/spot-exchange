package com.spot.websocket.mapper;


import com.spot.websocket.dto.user.AccountDto;
import com.spot.websocket.dto.user.OrderDto;
import com.spot.websocket.model.user.Account;
import com.spot.websocket.model.user.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mappings({
        @Mapping(constant = "updatedAccountEvent", target = "eventType"),
        @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "eventTime"),
        @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "lastUpdate"),
        @Mapping(source = "assets", target = "items")
    })
    AccountDto toAccountDto(Account account);

    @Mappings({
            @Mapping(constant = "updatedOrderEvent", target = "eventType"),
            @Mapping(expression = "java(java.time.Instant.now().toEpochMilli())", target = "eventTime"),
    })
    OrderDto toOrderDto(Order order);

}
