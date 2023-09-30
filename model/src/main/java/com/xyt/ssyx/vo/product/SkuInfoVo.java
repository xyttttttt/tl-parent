package com.xyt.ssyx.vo.product;

import com.xyt.ssyx.model.product.SkuAttrValue;
import com.xyt.ssyx.model.product.SkuImage;
import com.xyt.ssyx.model.product.SkuInfo;
import com.xyt.ssyx.model.product.SkuPoster;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class SkuInfoVo extends SkuInfo {

	@ApiModelProperty(value = "海报列表")
	private List<SkuPoster> skuPosterList;

	@ApiModelProperty(value = "属性值")
	private List<SkuAttrValue> skuAttrValueList;

	@ApiModelProperty(value = "图片")
	private List<SkuImage> skuImagesList;

}

