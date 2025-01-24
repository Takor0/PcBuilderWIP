import {BASE_URL} from "@/constants/common";
import {request} from "@/utils/request";

const SET_ENDPOINTS = {
    base: `${BASE_URL}api/builds`,
    generate: () => `${SET_ENDPOINTS.base}/generate`
}

export enum CpuPreference {
    AMD = "AMD",
    INTEL = "INTEL",
    NO_PREFERENCE = "NO_PREFERENCE"
}

export enum GpuPreference {
    NVIDIA = "NVIDIA",
    AMD = "AMD",
    NO_PREFERENCE = "NO_PREFERENCE"
}

export enum ComputerUsage {
    GAMING_AAA = "GAMING_AAA",
    GAMING_ESPORT = "GAMING_ESPORT",
    PROGRAMMING = "PROGRAMMING",
    GRAPHICS_RENDERING = "GRAPHICS_RENDERING",
    STREAMING = "STREAMING",
    OFFICE_WORK = "OFFICE_WORK"
}

export enum PerformancePriority {
    CPU_PRIORITY = "CPU_PRIORITY",
    GPU_PRIORITY = "GPU_PRIORITY",
    BALANCED = "BALANCED"
}

export enum AdditionalRequirement {
    QUIET_OPERATION = "QUIET_OPERATION",
    OVERCLOCKING = "OVERCLOCKING",
    RGB = "RGB",
    WIFI = "WIFI",
    SMALL_FORM_FACTOR = "SMALL_FORM_FACTOR"
}

export interface GeneratePayload {
    budget: number
    cpuPreference: CpuPreference
    gpuPreference: GpuPreference
    usage: ComputerUsage
    priority: PerformancePriority
    requirements: Array<AdditionalRequirement>
}


class SetService {
    async generate(generatePayload: GeneratePayload) {
        const {data, res} = await request({
            url: SET_ENDPOINTS.generate(),
            body: generatePayload,
            method: 'POST'
        })
        return data
    }
    async saveSet(setId: number) {
        return 1
        return await request({
            url: `${SET_ENDPOINTS.base}/${setId}`,
            method: 'POST'
        })
    }
}
export default new SetService()