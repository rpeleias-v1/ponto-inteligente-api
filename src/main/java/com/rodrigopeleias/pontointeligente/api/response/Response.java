package com.rodrigopeleias.pontointeligente.api.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@NoArgsConstructor
public class Response<T> {

    @Getter
    private T Data;

    private List<String> errors;

    public List<String> getErrors() {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        return errors;
    }
}
