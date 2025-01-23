import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { useUrlBuilder } from '@/common/hooks/useUrl';
import TisPatResult from '@/types/table/tisPatResult';
import { API_CONTEXT } from '../config';

const { buildGetUrl } = useUrlBuilder();

export interface TisPatResultData extends TisPatResult {
  __cascade_add_temp_id__?: string | number | undefined;
}

export default class TisPatResultController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<TisPatResultData>>(API_CONTEXT + '/app/tisPatResult/list', params, httpOptions);
  }
  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<TisPatResultData>(API_CONTEXT + '/app/tisPatResult/view', params, httpOptions);
  }
  static export(params: ANY_OBJECT, fileName: string) {
    return super.download(API_CONTEXT + '/app/tisPatResult/export', params, fileName);
  }
  static import(params: ANY_OBJECT) {
    return super.upload(API_CONTEXT + '/app/tisPatResult/import', params);
  }
  static printUrl(params: ANY_OBJECT) {
    return buildGetUrl(API_CONTEXT + '/app/tisPatResult/print', params);
  }
  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatResult/add', params, httpOptions);
  }
  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatResult/update', params, httpOptions);
  }
  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatResult/delete', params, httpOptions);
  }
  static deleteBatch(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/app/tisPatResult/deleteBatch', params, httpOptions);
  }
}
