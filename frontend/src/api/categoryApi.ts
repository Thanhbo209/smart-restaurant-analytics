import api from "./axiosInstance";

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}

export interface Category {
  id: number;

  name: string;
  slug: string;

  description?: string;

  isActive: boolean;

  createdAt?: string;
  updatedAt?: string;
}

export interface CategoryParent {
  id: number;
  name: string;
  slug: string;
}

export interface CategoryChild {
  id: number;
  name: string;
  slug: string;

  isActive: boolean;
}

export interface CategoryResponse extends Category {
  parent?: CategoryParent | null;
}

export interface CategoryTreeResponse extends Category {
  children: CategoryTreeResponse[];
}

export interface CategoryRequest {
  name: string;

  description?: string;

  parentId?: number | null;
}

export interface CategoryToggleRequest {
  isActive: boolean;
}

export interface CategoryQueryParams {
  rootOnly?: boolean;
}

export const categoryApi = {
  async getTree() {
    const res =
      await api.get<ApiResponse<CategoryTreeResponse[]>>("/categories/tree");

    return res.data.data;
  },

  async getAll(params?: CategoryQueryParams) {
    const res = await api.get<ApiResponse<CategoryResponse[]>>("/categories", {
      params,
    });

    return res.data.data;
  },

  async getById(id: number) {
    const res = await api.get<ApiResponse<CategoryResponse>>(
      `/categories/${id}`,
    );

    return res.data.data;
  },

  async create(body: CategoryRequest) {
    const res = await api.post<ApiResponse<CategoryResponse>>(
      "/categories",
      body,
    );

    return res.data.data;
  },

  async update(id: number, body: CategoryRequest) {
    const res = await api.put<ApiResponse<CategoryResponse>>(
      `/categories/${id}`,
      body,
    );

    return res.data.data;
  },

  async toggle(id: number, body: CategoryToggleRequest) {
    const res = await api.patch<ApiResponse<void>>(
      `/categories/${id}/toggle`,
      body,
    );

    return res.data;
  },

  async remove(id: number) {
    const res = await api.delete<ApiResponse<void>>(`/categories/${id}`);

    return res.data;
  },
};
