package kr.co.koreazinc.spring.model;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileInfo implements Closeable {

    @Schema(description = "파일")
    private InputStream file;

    @Schema(description = "시스템")
    private String system;

    @Schema(description = "법인")
    private String corporation;

    @Schema(description = "파일 경로")
    private String path;

    @Schema(description = "파일 이름")
    private String name;

    @Schema(description = "파일 크기")
    private Long size;

    @Override
    public void close() throws IOException {
        this.file.close();
    }

    public static class FileInfoBuilder {

        public FileInfoBuilder builder(MultipartFile file) throws IOException {
            this.file = file.getInputStream();
            this.name = file.getOriginalFilename();
            this.size = file.getSize();
            return this;
        }
    }
}