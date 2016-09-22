package org.matt.budget.models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.matt.budget.models.util.Iso8601DateTimeFormatAdapter;
import org.matt.budget.persistence.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "token")
@XmlRootElement
@ToString(exclude="user")
@XmlAccessorType(XmlAccessType.NONE)
public class Token implements BaseEntity {

  public static final String COL_ID = "id";
  public static final String COL_TOKEN = "token";
  public static final String COL_EXPIRE_DATE = "expire_date";

  @Id
  @GeneratedValue(generator = "token_seq")
  @SequenceGenerator(name = "token_seq", sequenceName = "TOKEN_SEQ", allocationSize = 1, initialValue = 100)
  @XmlElement
  private Integer id;

  @Column(name = COL_TOKEN)
  @XmlElement
  private String token;

  @ManyToOne(optional = false)
  @XmlTransient
  private User user;

  @Column(name = COL_EXPIRE_DATE)
  @Temporal(TemporalType.TIMESTAMP)
  @XmlElement(name = "expire-date", nillable = false)
  @XmlJavaTypeAdapter(Iso8601DateTimeFormatAdapter.class)
  private Date expireDate;
}
