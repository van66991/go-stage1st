package live.nanami.gos1.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author arichi
 * @since 2024/12/17
 */
@Data
@Builder
public class GetStage1stPageInfoParam {
    boolean needDispatch;
    String pageNum;
    String buildingNum;
}

