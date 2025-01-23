import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { useUrlBuilder } from '@/common/hooks/useUrl';
import TisPatInfo from '@/types/table/tisPatInfo';
import { TisPatResultData } from '@/api/generated/tisPatResultController';
import { API_CONTEXT } from '../config';

const { buildGetUrl } = useUrlBuilder();

export interface TisPatInfoData extends TisPatInfo {
  tisPatResultList?: TisPatResultData[];
  __cascade_add_temp_id__?: string | number | undefined;
}

export default class TisPatInfoController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<TisPatInfoData>>(API_CONTEXT + '/app/tisPatInfo/list', params, httpOptions);
  }
  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<TisPatInfoData>(API_CONTEXT + '/app/tisPatInfo/view', params, httpOptions);
  }
  static export(params: ANY_OBJECT, fileName: string) {
    return super.download(API_CONTEXT + '/app/tisPatInfo/export', params, fileName);
  }
  static import(params: ANY_OBJECT) {
    return super.upload(API_CONTEXT + '/app/tisPatInfo/import', params);
  }
  static printUrl(params: ANY_OBJECT) {
    return buildGetUrl(API_CONTEXT + '/app/tisPatInfo/print', params);
  }
  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatInfo/add', params, httpOptions);
  }
  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatInfo/update', params, httpOptions);
  }
  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatInfo/delete', params, httpOptions);
  }
  static deleteBatch(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatInfo/deleteBatch', params, httpOptions);
  }
}
