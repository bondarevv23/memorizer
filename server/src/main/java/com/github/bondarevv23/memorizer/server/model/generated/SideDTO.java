package com.github.bondarevv23.memorizer.server.model.generated;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * SideDTO
 */
@lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@lombok.AllArgsConstructor
public class SideDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private String title;

  private String text = null;

  private String imageLink = null;

  /**
   * Default constructor
   * @deprecated Use {@link SideDTO#SideDTO(String)}
   */
  @Deprecated
  public SideDTO() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public SideDTO(String title) {
    this.title = title;
  }

  public SideDTO title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  @NotNull @Size(max = 100) 
  @Schema(name = "title", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public SideDTO text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  @Size(max = 320) 
  @Schema(name = "text", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public SideDTO imageLink(String imageLink) {
    this.imageLink = imageLink;
    return this;
  }

  /**
   * Get imageLink
   * @return imageLink
  */
  @Size(max = 60) 
  @Schema(name = "imageLink", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("imageLink")
  public String getImageLink() {
    return imageLink;
  }

  public void setImageLink(String imageLink) {
    this.imageLink = imageLink;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SideDTO sideDTO = (SideDTO) o;
    return Objects.equals(this.title, sideDTO.title) &&
        Objects.equals(this.text, sideDTO.text) &&
        Objects.equals(this.imageLink, sideDTO.imageLink);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, text, imageLink);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SideDTO {\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    imageLink: ").append(toIndentedString(imageLink)).append("\n");
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

