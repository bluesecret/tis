import {BaseController} from '@/api/BaseController';
import {RequestOption} from '@/common/http/types';
import {ANY_OBJECT} from '@/types/generic';
import {TableData} from '@/common/types/table';
import {useUrlBuilder} from '@/common/hooks/useUrl';
import TisPatResult from '@/types/table/tisPatResult';
import {API_CONTEXT} from '../config';

const {buildGetUrl} = useUrlBuilder();

export interface TisDeviceData extends TisPatResult {
  __cascade_add_temp_id__?: string | number | undefined;
}

export default class TisDeviceInfoController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<TisDeviceData>>(API_CONTEXT + '/app/tisDeviceInfo/list', params, httpOptions);
  }

  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisDeviceInfo/add', params, httpOptions);
  }

  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisDeviceInfo/update', params, httpOptions);
  }

  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisDeviceInfo/delete', params, httpOptions);
  }

  static uploadFile(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.upload(API_CONTEXT + '/app/tisDeviceInfo/upload', params, httpOptions);
  }

  static userBind(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisDeviceInfo/userBind', params, httpOptions);
  }
}
