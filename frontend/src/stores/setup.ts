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
        getRequest(result) {
            let payload
            payload = {
                budget: result['budget'] || 5000,
                cpuPreference: result?.req?.['cpuPreference'] || 'NO_PREFERENCE',
                gpuPreference: result?.req?.['gpuPreference'] || 'NO_PREFERENCE',
                usage: result?.['appliance'] || 'GAMING_AAA',
                priority: result?.requirements?.['priority'] || 'BALANCED',
                requirements: result?.requirements?.['requirements'] || []
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