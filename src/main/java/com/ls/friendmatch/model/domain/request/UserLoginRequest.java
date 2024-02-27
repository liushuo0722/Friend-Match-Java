package com.ls.friendmatch.model.domain.request;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserLoginRequest implements Serializable {


    private static final long serialVersionUID = -7307233312359293497L;

    private String userAccount ;

    private String userPassword ;


}
