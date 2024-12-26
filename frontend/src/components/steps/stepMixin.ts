import { useSetupStore } from '@/stores/setup'

export default {
    data() {
        return {
            setupStore: null as any,
            title: null as string | null,
            content: null as any
        }
    },
    mounted() {
        this.setupStore = useSetupStore()
    },
    methods: {
        storeResult() {
            this.setupStore.storeStepResult(this.title, this.content)
        },
    }
}