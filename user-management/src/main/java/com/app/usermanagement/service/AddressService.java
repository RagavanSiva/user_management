package com.app.usermanagement.service;

import com.app.usermanagement.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {

    List<AddressDto> getAddresses(String userId);

    AddressDto getAddress(String addressId);
}
