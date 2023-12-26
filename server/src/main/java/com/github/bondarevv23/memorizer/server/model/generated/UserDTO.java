package com.github.bondarevv23.memorizer.server.model.generated;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * UserDTO
 */
@lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class UserDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Long tgId;

  @Valid
  private List<Integer> deckIds = new ArrayList<>();

  /**
   * Default constructor
   * @deprecated Use {@link UserDTO#UserDTO(Long, List<Integer>)}
   */
  @Deprecated
  public UserDTO() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public UserDTO(Long tgId, List<Integer> deckIds) {
    this.tgId = tgId;
    this.deckIds = deckIds;
  }

  public UserDTO tgId(Long tgId) {
    this.tgId = tgId;
    return this;
  }

  /**
   * Get tgId
   * @return tgId
  */
  @NotNull 
  @Schema(name = "tgId", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("tgId")
  public Long getTgId() {
    return tgId;
  }

  public void setTgId(Long tgId) {
    this.tgId = tgId;
  }

  public UserDTO deckIds(List<Integer> deckIds) {
    this.deckIds = deckIds;
    return this;
  }

  public UserDTO addDeckIdsItem(Integer deckIdsItem) {
    if (this.deckIds == null) {
      this.deckIds = new ArrayList<>();
    }
    this.deckIds.add(deckIdsItem);
    return this;
  }

  /**
   * Get deckIds
   * @return deckIds
  */
  @NotNull 
  @Schema(name = "deckIds", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("deckIds")
  public List<Integer> getDeckIds() {
    return deckIds;
  }

  public void setDeckIds(List<Integer> deckIds) {
    this.deckIds = deckIds;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserDTO userDTO = (UserDTO) o;
    return Objects.equals(this.tgId, userDTO.tgId) &&
        Objects.equals(this.deckIds, userDTO.deckIds);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tgId, deckIds);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class UserDTO {\n");
    sb.append("    tgId: ").append(toIndentedString(tgId)).append("\n");
    sb.append("    deckIds: ").append(toIndentedString(deckIds)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

