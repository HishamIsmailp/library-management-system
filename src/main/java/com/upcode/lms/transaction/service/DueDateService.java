package com.upcode.lms.transaction.service;

import com.upcode.lms.common.utils.DateUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class DueDateService {

    public LocalDate calculateDueDate(int days){
        return DateUtils.calculateDueDate(days);
    }
    
}
