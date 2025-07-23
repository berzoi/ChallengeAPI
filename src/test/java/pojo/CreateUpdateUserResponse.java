package pojo;

import lombok.Data;

@Data
public class CreateUpdateUserResponse {

  private String name;
  private String job;
  private Integer id;
  private String createdAt;
  private String updatedAt;
}
