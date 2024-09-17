package net.mlorenzo.encuestabackend.repositories;

// Interfaz para que JPA pueda hacer el mapeo del resultado de la cosulta personalizada "getPollResults"

public interface PollResult {
	Integer getQuestionOrder();
	Long getQuestionId();
	String getQuestion();
	Long getAnswerId();
	String getAnswer();
	Long getResult();
}
