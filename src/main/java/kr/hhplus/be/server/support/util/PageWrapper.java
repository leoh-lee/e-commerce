package kr.hhplus.be.server.support.util;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class PageWrapper<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> content;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;

    public PageWrapper(Page<T> page) {
        this.content = page.getContent();
        this.totalPages = page.getTotalPages();
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getPageable().isPaged() ? page.getPageable().getPageNumber() : 0;
        this.pageSize = page.getPageable().isPaged() ? page.getPageable().getPageSize() : 10;
    }

    public Page<T> toPage(int pageNumber, int pageSize) {
        return new PageImpl<>(content, PageRequest.of(pageNumber, pageSize), totalElements);
    }
}

