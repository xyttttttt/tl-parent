package com.xyt.ssyx.order.blockHandler;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.xyt.ssyx.common.exception.SsyxException;
import com.xyt.ssyx.common.result.ResultCodeEnum;
import com.xyt.ssyx.vo.order.OrderSubmitVo;

public class OrderSubmitBlockHandler {


    public Long submitOrderBlockHandler(BlockException blockException) {

        throw new SsyxException(ResultCodeEnum.SERVICE_ERROR);
    }
}
