package com.ubt.identityservice.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationEmailModel
{
    private String firstName;
    private String lastName;
    private String url;
}