/**
 * 
 */
package com.aiear.config.session;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_hospital_info")
public class User {
    @Id
    private String hospitalId;
    private String hospitalNm;
    private String hospitalPwd;
    private String userType;
}