import { defineStore } from 'pinia'
export type { GeneratePayload } from '@/services/setService'

interface Result {
    appliance?: string
}

export const useSetupStore = defineStore('setup', {
    state: () => {
        return { result: {} as { [key: string]: Result } }
    },
    actions: {
        getRequest() {
            let payload
            payload = {
                budget: this.result['budget'] || 5000,
                cpuPreference: this.result?.requirements?.['cpuPreference'] || 'NO_PREFERENCE',
                gpuPreference: this.result?.requirements?.['gpuPreference'] || 'NO_PREFERENCE',
                usage: this.result?.['appliance'] || 'GAMING_AAA',
                priority: this.result?.requirements?.['priority'] || 'BALANCED',
                requirements: this.result?.requirements?.['requirements'] || []
            }
            return payload
        },
        clear() {
            this.result = {}
        },
        storeStepResult(title: string, content: any) {
            this.result[title] = content
        },
    },
})

// {
//     "null": 3015,
//     "appliance": "GAMING_AAA",
//     "requirements": {
//     "cpuPreference": "INTEL",
//         "gpuPreference": "NVIDIA",
//         "priority": "GPU_PRIORITY",
//         "requirements": [
//         "QUIET_OPERATION",
//         "OVERCLOCKING"
//     ]
// }
// }