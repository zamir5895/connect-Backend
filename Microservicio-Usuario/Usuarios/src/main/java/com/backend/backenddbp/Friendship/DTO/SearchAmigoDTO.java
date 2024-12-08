package com.backend.backenddbp.Friendship.DTO;

import com.amazonaws.services.autoscaling.model.LoadBalancerTargetGroupState;
import lombok.Data;

import java.util.Objects;

@Data
public class SearchAmigoDTO {
    private String amigoName;
    private Long amigoId;
    private Long amistadId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchAmigoDTO amigoDTO = (SearchAmigoDTO) o;
        return Objects.equals(amigoId, amigoDTO.getAmigoId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(amigoId);
    }
}
