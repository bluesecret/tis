import { BaseController } from '@/api/BaseController';
import { RequestOption } from '@/common/http/types';
import { ANY_OBJECT } from '@/types/generic';
import { TableData } from '@/common/types/table';
import { useUrlBuilder } from '@/common/hooks/useUrl';
import SysDept from '@/types/table/sysDept';
import { API_CONTEXT } from '../config';

const { buildGetUrl } = useUrlBuilder();

export interface SysDeptData extends SysDept {
  __cascade_add_temp_id__?: string | number | undefined;
}

export default class SysDeptController extends BaseController {
  static list(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post<TableData<SysDeptData>>(API_CONTEXT + '/upms/sysDept/list', params, httpOptions);
  }
  static view(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.get<SysDeptData>(API_CONTEXT + '/upms/sysDept/view', params, httpOptions);
  }
  static export(params: ANY_OBJECT, fileName: string) {
    return super.download(API_CONTEXT + '/upms/sysDept/export', params, fileName);
  }
  static import(params: ANY_OBJECT) {
    return super.upload(API_CONTEXT + '/upms/sysDept/import', params);
  }
  static printUrl(params: ANY_OBJECT) {
    return buildGetUrl(API_CONTEXT + '/upms/sysDept/print', params);
  }
  static add(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/upms/sysDept/add', params, httpOptions);
  }
  static update(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/upms/sysDept/update', params, httpOptions);
  }
  static delete(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/upms/sysDept/delete', params, httpOptions);
  }
  static deleteBatch(params: ANY_OBJECT, httpOptions?: RequestOption) {
    return super.post(API_CONTEXT + '/upms/sysDept/deleteBatch', params, httpOptions);
  }
}
