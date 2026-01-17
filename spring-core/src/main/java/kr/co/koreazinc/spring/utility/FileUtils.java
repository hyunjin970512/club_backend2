package kr.co.koreazinc.spring.utility;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Base64;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.SystemUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import kr.co.koreazinc.spring.exception.TokenIssuanceException;
import kr.co.koreazinc.spring.model.FileInfo;
import kr.co.koreazinc.spring.security.property.OAuth2Property;
import kr.co.koreazinc.spring.util.CommonMap;

@Component
public class FileUtils {

    public static final String BASE_PATH = PropertyUtils.getProperty("system.file-path", (SystemUtils.IS_OS_WINDOWS ? "C:\\file" : "/opt/file"));

    private static final String[] UNIT = { "bytes", "KB", "MB", "GB", "TB", "PB" };

    public static String byteCalculation(long size) {
        if (ObjectUtils.isEmpty(size)) return "0 " + UNIT[0];

        int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
        DecimalFormat df = new DecimalFormat("#,###.##");
        double ret = ((size / Math.pow(1024, Math.floor(idx))));
        return df.format(ret) + " " + UNIT[idx];
    }

    public static File mkdir(String path) {
        path = Optional.ofNullable(path).orElse(FileUtils.BASE_PATH);
        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static String encodeToBase64(File file) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    public static String encodeToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    // public synchronized FileDto.Output upload(String filePath, FileDto.Input dto) throws IOException {
    //     try (dto) {
    //         File file = new File(filePath + FileUtils.FILE_SEPARATOR + dto.getName());
    //         FileUtils.copyInputStreamToFile(dto.getFile(), file);
    //         return new FileDto.Output(file);
    //     }
    // }

    // public synchronized FileDto.Output download(String fullPath) throws IOException {
    //     File file = new File(fullPath);
    //     if (!file.exists()) throw new IOException("Not find file");
    //     return new FileDto.Output(file);
    // }

    public static FileInfo remoteUpload(OAuth2Property.Credential credential, FileInfo file) throws IOException {
        try {
            MultipartBodyBuilder multipart = new MultipartBodyBuilder();
            multipart.part("path", Stream.of(file.getPath().split("[\\/]")).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining("/")));
            multipart.part("file", new InputStreamResource(file.getFile())).filename(file.getName());

            CommonMap result = WebClient.builder()
                .baseUrl(credential.getBaseUrl())
                .build()
                .post()
                .uri(uriBuilder->uriBuilder.path(String.format("/%s/%s/upload.json", file.getSystem(), file.getCorporation())).build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + OAuthUtils.issuedToken(credential.getTokenUrl(), credential.getClientId(), credential.getClientSecret(), credential.getScope()))
                .body(BodyInserters.fromMultipartData(multipart.build()))
                .exchangeToMono(response->{
                    return response.bodyToMono(CommonMap.class);
                }).block();

            return FileInfo.builder()
                .path(String.valueOf(result.get("canonicalPath")).replace(String.valueOf(result.get("name")), ""))
                .name(String.valueOf(result.get("name")))
                .build();
        } catch (TokenIssuanceException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    public static InputStream remoteDownload(OAuth2Property.Credential credential, FileInfo file) throws IOException {
        try {
            InputStreamResource result = WebClient.builder()
                .baseUrl(credential.getBaseUrl())
                .codecs(c -> c.defaultCodecs().maxInMemorySize(50*1024*1024))
                .build()
                .get()
                .uri(uriBuilder->uriBuilder.path(String.format("/%s/%s/download.json", file.getSystem(), file.getCorporation()))
                .queryParam("filepath", Stream.of(file.getPath().split("[\\/]")).filter(ObjectUtils::isNotEmpty).collect(Collectors.joining("/")) + "/" + file.getName()).build())
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .header("Authorization", "Bearer " + OAuthUtils.issuedToken(credential.getTokenUrl(), credential.getClientId(), credential.getClientSecret(), credential.getScope()))
                .exchangeToMono(response->{
                    return response.bodyToMono(new ParameterizedTypeReference<InputStreamResource>() {});
                }).block();

            return result.getInputStream();
        } catch (TokenIssuanceException e) {
            throw new IOException(e);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }
}