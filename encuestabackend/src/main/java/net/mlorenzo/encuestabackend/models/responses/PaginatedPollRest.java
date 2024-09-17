package net.mlorenzo.encuestabackend.models.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaginatedPollRest {
	private List<PollRest> polls;
	private Integer totalPages;
	private Long totalRecords;
	private Integer currentPageRecords;
	private Integer currentPage;
}
