package com.github.bondarevv23.memorizer.server.model.generated;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.github.bondarevv23.memorizer.server.model.generated.SideDTO;
import java.io.Serializable;
import java.time.OffsetDateTime;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import jakarta.annotation.Generated;

/**
 * CardDTO
 */
@lombok.Builder

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
public class CardDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  private Integer id;

  private Integer deckId;

  private SideDTO question;

  private SideDTO answer;

  /**
   * Default constructor
   * @deprecated Use {@link CardDTO#CardDTO(Integer, Integer, SideDTO, SideDTO)}
   */
  @Deprecated
  public CardDTO() {
    super();
  }

  /**
   * Constructor with only required parameters
   */
  public CardDTO(Integer id, Integer deckId, SideDTO question, SideDTO answer) {
    this.id = id;
    this.deckId = deckId;
    this.question = question;
    this.answer = answer;
  }

  public CardDTO id(Integer id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  @NotNull 
  @Schema(name = "id", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("id")
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public CardDTO deckId(Integer deckId) {
    this.deckId = deckId;
    return this;
  }

  /**
   * Get deckId
   * @return deckId
  */
  @NotNull 
  @Schema(name = "deckId", example = "1234567890", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("deckId")
  public Integer getDeckId() {
    return deckId;
  }

  public void setDeckId(Integer deckId) {
    this.deckId = deckId;
  }

  public CardDTO question(SideDTO question) {
    this.question = question;
    return this;
  }

  /**
   * Get question
   * @return question
  */
  @NotNull @Valid 
  @Schema(name = "question", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("question")
  public SideDTO getQuestion() {
    return question;
  }

  public void setQuestion(SideDTO question) {
    this.question = question;
  }

  public CardDTO answer(SideDTO answer) {
    this.answer = answer;
    return this;
  }

  /**
   * Get answer
   * @return answer
  */
  @NotNull @Valid 
  @Schema(name = "answer", requiredMode = Schema.RequiredMode.REQUIRED)
  @JsonProperty("answer")
  public SideDTO getAnswer() {
    return answer;
  }

  public void setAnswer(SideDTO answer) {
    this.answer = answer;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CardDTO cardDTO = (CardDTO) o;
    return Objects.equals(this.id, cardDTO.id) &&
        Objects.equals(this.deckId, cardDTO.deckId) &&
        Objects.equals(this.question, cardDTO.question) &&
        Objects.equals(this.answer, cardDTO.answer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, deckId, question, answer);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CardDTO {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    deckId: ").append(toIndentedString(deckId)).append("\n");
    sb.append("    question: ").append(toIndentedString(question)).append("\n");
    sb.append("    answer: ").append(toIndentedString(answer)).append("\n");
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

