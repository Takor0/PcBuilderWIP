import {BASE_URL} from "@/constants/common";
import {request} from "@/utils/request";

const SET_ENDPOINTS = {
    base: `${BASE_URL}builds`,
    generate: () => `${SET_ENDPOINTS.base}/generate`
}

enum CpuPreference {
    AMD, INTEL, NO_PREFERENCE
}

enum GpuPreference {
    NVIDIA, AMD, NO_PREFERENCE
}

enum ComputerUsage {
    GAMING_AAA,
    GAMING_ESPORT,
    PROGRAMMING,
    GRAPHICS_RENDERING,
    STREAMING,
    OFFICE_WORK
}

enum PerformancePriority {
    CPU_PRIORITY,
    GPU_PRIORITY,
    BALANCED
}

enum AdditionalRequirement {
    QUIET_OPERATION,
    OVERCLOCKING,
    RGB,
    WIFI,
    SMALL_FORM_FACTOR
}

interface GeneratePayload {
    budget: number
    cpuPreference: CpuPreference
    gpuPreference: GpuPreference
    usage: ComputerUsage
    performancePriority: PerformancePriority
    requirements: Array<AdditionalRequirement>
}


class SetService {
    async generate(generatePayload: GeneratePayload) {
        return [
            {
                "name": "Wariant A",
                "totalPrice": 4850.00,
                "components": {
                    "cpu": {
                        "name": "AMD Ryzen 5 5600X",
                        "manufacturer": "AMD",
                        "price": 899.00,
                        "coreCount": 6,
                        "coreClock": 3.7,
                        "boostClock": 4.6,
                        "socket": "AM4"
                    },
                    "motherboard": {
                        "name": "MSI B550 GAMING PLUS",
                        "manufacturer": "MSI",
                        "price": 599.00,
                        "socket": "AM4",
                        "chipset": "B550",
                        "formFactor": "ATX"
                    },
                    "videoCard": {
                        "name": "NVIDIA GeForce RTX 3060",
                        "manufacturer": "NVIDIA",
                        "price": 1899.00,
                        "memory": 12,
                        "coreClock": 1320,
                        "boostClock": 1777
                    },
                    "hardDrive": {
                        "name": "Samsung 970 EVO Plus",
                        "manufacturer": "Samsung",
                        "price": 399.00,
                        "capacity": 1000,
                        "type": "SSD",
                        "interface": "NVMe"
                    },
                    "pcCase": {
                        "name": "be quiet! Pure Base 500DX",
                        "manufacturer": "be quiet!",
                        "price": 449.00,
                        "formFactor": "ATX",
                        "hasRgb": true
                    },
                    "powerSupply": {
                        "name": "be quiet! Straight Power 11",
                        "manufacturer": "be quiet!",
                        "price": 599.00,
                        "wattage": 650,
                        "efficiency": "80PLUS_GOLD"
                    }
                }
            }
        ]


        const {data, res} = await request({
            url: SET_ENDPOINTS.generate(),
            body: generatePayload
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