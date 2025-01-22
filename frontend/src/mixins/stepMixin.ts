import { useSetupStore } from '@/stores/setup'

export default {
    emits: ['next'],
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
        clearSetup() {
            this.setupStore.clear()
        },
        nextStep() {
            this.$emit('next')
        }
    }
}