import { defineStore } from 'pinia'

interface Result {
    appliance?: string
}

export const useSetupStore = defineStore('setup', {
    state: () => {
        return { result: {} as { [key: string]: Result } }
    },
    actions: {
        storeStepResult(title: string, content: any) {
            this.result[title] = content
        },
    },
})